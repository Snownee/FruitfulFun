#!/usr/bin/env python3
"""Convert Blockbench hat exports into Fruitful Fun resource files.

For each hat name it reads:
    art/hats/<name>.json    Java block model exported from Blockbench
    art/hats/<name>.png     texture

and writes:
    src/main/resources/assets/fruitfulfun/models/block/<name>_hat.json
    src/main/resources/assets/fruitfulfun/textures/block/<name>_hat.png
    src/main/resources/resourcepacks/cosmetic/assets/fruitfulfun/trinkets/<name>_hat.json

The trinket definitions live in the embedded "cosmetic" data pack
(src/main/resources/resourcepacks/cosmetic), which CommonProxy only registers
when the cosmetic module is loaded.

Authoring convention
--------------------
The Blockbench project must contain a hidden player-head reference cube placed
at (4,4,4)-(12,12,12) (8x8x8, centred on the block centre), in a group named
"steve".  Author the hat around it, then hide/export it.  Any element named
"head" or listed in the "steve" group is stripped as that reference;
"#missing" faces are stripped from the remaining elements.

Because every hat shares the same reference, a single uniform transform maps it
onto the real head: display.head.scale defaults to 1.6 but an authored value is
kept (author in Blockbench to size the hat), and the authored head rotation is
kept.  The trinket "head" entry (player) therefore matches the vanilla head
slot.

Configuration (convert_hats.json)
-------------------------
The bee trinket ("bone" entry) is tuned per hat from ``convert_hats.json`` in the
project root::

    {
      "default": { "forward": 10, "left": 0, "yaw": 0, "scale": 1.0, "translate": [0, 0, 0] },
      "mushroom": { "forward": 10, "left": 0, "yaw": 0, "scale": 1.0, "translate": [0, 0, 0] }
    }

    forward / left / yaw  degrees; the bee gets the INVERSE of
                          rotationXYZ(forward, yaw, left), so it cancels the
                          same rotation applied to the model.  With the current
                          10deg model tilt, forward=10 reproduces an upright hat.
    scale                 multiplier on the bone base scale (1.0 = as authored).
    translate             [x, y, z] in blocks, ADDED to the bone base position
                          ([0, 0, 0] = as authored).

Missing keys fall back to ``default``; missing hats fall back to ``default``.
If the file does not exist it is generated (default section + one entry per
art/hats/*.json not starting with '_').  Unknown hats/keys are ignored with a
warning.

Usage:
    python convert_hats.py                 # every non-underscore art/hats/*.json
    python convert_hats.py mushroom witch  # explicit names

Prefix a model file with '_' to keep it out of the default scan.
"""
import json
import math
import os
import shutil
import sys

ROOT = os.path.dirname(os.path.abspath(__file__))
ART = os.path.join(ROOT, "art", "hats")
RESOURCES = os.path.join(ROOT, "src", "main", "resources")
ASSETS = os.path.join(RESOURCES, "assets", "fruitfulfun")
OUT_MODEL = os.path.join(ASSETS, "models", "block")
OUT_TEX = os.path.join(ASSETS, "textures", "block")
# Hat trinket definitions live in the embedded "cosmetic" data pack, which is
# only registered when the cosmetic module is loaded.
PACK = os.path.join(RESOURCES, "resourcepacks", "cosmetic")
OUT_TRINKETS = os.path.join(PACK, "assets", "fruitfulfun", "trinkets")
CONFIG_PATH = os.path.join(ROOT, "convert_hats.json")

HEAD_SCALE = 1.6
BONE_BASE_SCALE = 0.4

CONFIG_KEYS = ("forward", "left", "yaw", "scale", "translate")
CONFIG_DEFAULT = {"forward": 10, "left": 0, "yaw": 0, "scale": 1.0, "translate": [0, 0, 0]}

# Trinkets base is scale(1,-1,-1); the vanilla head slot is
# rotateY(180)*scale(0.625,-0.625,-0.625).  Setting the transformation to
# 0.625 * rotateY(180) makes both paths identical.  Rotations use quaternion
# components [x,y,z,w] because the axis-angle form is in radians.
TRINKET_HEAD = {
    "translation": [0, 0, 0],
    "scale": [0.625, 0.625, 0.625],
    "left_rotation": [0, 1, 0, 0],
    "right_rotation": [0, 0, 0, 1],
}
TRINKET_BONE = {
    "translation": [0, 0.125, 0.2],
    "scale": [BONE_BASE_SCALE, BONE_BASE_SCALE, BONE_BASE_SCALE],
    "left_rotation": [0, 1, 0, 0],
    "right_rotation": [0, 0, 0, 1],
}


def quat_axis(axis, radians):
    h = radians / 2.0
    s, c = math.sin(h), math.cos(h)
    return {"x": (s, 0, 0, c), "y": (0, s, 0, c), "z": (0, 0, s, c)}[axis]


def quat_mul(p, q):
    x1, y1, z1, w1 = p
    x2, y2, z2, w2 = q
    return (w1 * x2 + x1 * w2 + y1 * z2 - z1 * y2,
            w1 * y2 - x1 * z2 + y1 * w2 + z1 * x2,
            w1 * z2 + x1 * y2 - y1 * x2 + z1 * w2,
            w1 * w2 - x1 * x2 - y1 * y2 - z1 * z2)


def rotation_inverse(rot):
    """Conjugate of Minecraft's new Quaternionf().rotationXYZ(rx, ry, rz)."""
    q = (0, 0, 0, 1)
    for axis, deg in zip("xyz", rot):
        q = quat_mul(q, quat_axis(axis, math.radians(deg)))
    conj = [round(-q[0], 7), round(-q[1], 7), round(-q[2], 7), round(q[3], 7)]
    return [0.0 if value == 0 else value for value in conj]


def hat_basenames():
    """Every art/hats/*.json whose file name does not start with '_'."""
    if not os.path.isdir(ART):
        return []
    return sorted(
        os.path.splitext(entry)[0]
        for entry in os.listdir(ART)
        if entry.endswith(".json") and not entry.startswith("_")
    )


def default_config():
    config = {"default": dict(CONFIG_DEFAULT)}
    for name in hat_basenames():
        config[name] = dict(CONFIG_DEFAULT)
    return config


def load_config():
    if not os.path.isfile(CONFIG_PATH):
        config = default_config()
        with open(CONFIG_PATH, "w", encoding="utf-8") as fh:
            json.dump(config, fh, indent=2)
            fh.write("\n")
        print("created %s" % CONFIG_PATH)
        return config
    with open(CONFIG_PATH, encoding="utf-8") as fh:
        config = json.load(fh)
    known = set(hat_basenames()) | {"default"}
    for key in config:
        if key not in known:
            print("WARN: unknown hat %r in %s, ignored" % (key, os.path.basename(CONFIG_PATH)))
    return config


def resolve_params(config, name):
    default = config.get("default", {})
    entry = config.get(name, {})
    if not isinstance(default, dict):
        print("WARN: 'default' must be an object, using built-in defaults")
        default = {}
    if not isinstance(entry, dict):
        print("WARN: entry %r must be an object, using defaults" % name)
        entry = {}
    params = {}
    for key in CONFIG_KEYS:
        if key in entry:
            params[key] = entry[key]
        elif key in default:
            params[key] = default[key]
        else:
            params[key] = CONFIG_DEFAULT[key]
    for where, obj in ((name, entry), ("default", default)):
        for key in obj:
            if key not in CONFIG_KEYS:
                print("WARN: unknown key %r in %r, ignored" % (key, where))
    return params


def resolve_translate(value):
    if (isinstance(value, (list, tuple)) and len(value) == 3
            and all(isinstance(v, (int, float)) for v in value)):
        return [float(v) for v in value]
    print("WARN: translate must be [x, y, z], got %r; using [0, 0, 0]" % (value,))
    return [0.0, 0.0, 0.0]


def strip_reference(model):
    """Drop the hidden player-head reference cube and unused fields.

    The reference is any element named "head" or listed in a group named
    "steve" (the hidden cube is meant to be hidden on export, but it is still
    in the JSON).  Faces pointing at the "#missing" texture are dropped as
    well -- they would render as the purple/black missing texture -- and an
    element left with no faces is removed.
    """
    reference = set()
    for group in model.get("groups", []):
        if group.get("name") == "steve":
            reference.update(group.get("children", []))
    kept = []
    for index, el in enumerate(model.get("elements", [])):
        if index in reference or el.get("name") == "head":
            continue
        faces = {d: f for d, f in el.get("faces", {}).items() if f.get("texture") != "#missing"}
        if not faces:
            continue
        el["faces"] = faces
        el.pop("name", None)
        el.pop("color", None)
        kept.append(el)
    model["elements"] = kept
    return model


def build_model(name, model):
    strip_reference(model)
    model.pop("format_version", None)
    model.pop("credit", None)
    model.pop("groups", None)
    texture = "fruitfulfun:block/%s_hat" % name
    # Keep whatever slot names the faces reference (they are usually "0", but
    # e.g. the straw hat uses "1") and point them all at the hat texture.
    slots = {key: texture for key in model.get("textures", {})}
    slots.setdefault("0", texture)
    slots["particle"] = texture
    model["textures"] = slots
    display = model.setdefault("display", {})
    display.setdefault("head", {}).setdefault("scale", [HEAD_SCALE, HEAD_SCALE, HEAD_SCALE])
    return model


def build_trinket(item_id, bone_rotation, bone_scale, bone_translation):
    # The player head path (vanilla + trinket "head") keeps the authored tilt so
    # it matches the vanilla head slot.  The bee ("bone") is driven by the config.
    bone = dict(TRINKET_BONE)
    bone["right_rotation"] = bone_rotation
    bone["scale"] = [bone_scale, bone_scale, bone_scale]
    bone["translation"] = bone_translation
    return {
        "target": ["fruitfulfun:%s" % item_id],
        "render": [
            dict(
                type="item",
                model_part="head",
                display_context="head",
                transformation=TRINKET_HEAD,
            ),
            dict(
                type="item",
                model_part="bone",
                display_context="head",
                transformation=bone,
            ),
        ],
    }


def convert(name, config):
    item_id = "%s_hat" % name
    src_json = os.path.join(ART, "%s.json" % name)
    src_png = os.path.join(ART, "%s.png" % name)
    if not os.path.isfile(src_json):
        print("skip %s: %s not found" % (name, src_json))
        return
    with open(src_json, encoding="utf-8") as fh:
        model = json.load(fh)
    build_model(name, model)
    with open(os.path.join(OUT_MODEL, "%s.json" % item_id), "w", encoding="utf-8") as fh:
        json.dump(model, fh, indent=2)
        fh.write("\n")
    if os.path.isfile(src_png):
        shutil.copyfile(src_png, os.path.join(OUT_TEX, "%s.png" % item_id))

    params = resolve_params(config, name)
    bone_rotation = rotation_inverse([params["forward"], params["yaw"], params["left"]])
    bone_scale = round(BONE_BASE_SCALE * params["scale"], 7)
    offset = resolve_translate(params["translate"])
    bone_translation = [round(base + delta, 7) for base, delta in zip(TRINKET_BONE["translation"], offset)]
    with open(os.path.join(OUT_TRINKETS, "%s.json" % item_id), "w", encoding="utf-8") as fh:
        json.dump(build_trinket(item_id, bone_rotation, bone_scale, bone_translation), fh, indent="\t")
        fh.write("\n")
    print("converted %s -> %s (%d elements, forward=%s left=%s yaw=%s scale=%s translate=%s)" % (
        src_json, item_id, len(model["elements"]),
        params["forward"], params["left"], params["yaw"], params["scale"], offset))


def main():
    config = load_config()
    names = sys.argv[1:] or hat_basenames()
    for name in names:
        convert(name, config)


if __name__ == "__main__":
    main()
