def fdCutting(action): walk(
    if type == "object" and has("type") and ."type" == "farmersdelight:tool" and ."tag" == "c:tools/axes" then
        ."type" = "farmersdelight:tool_action"
        | ."action" = action
        | del(."tag")
    end
);
if has("fabric:load_conditions") then
    ."conditions" = ."fabric:load_conditions"
    | del(."fabric:load_conditions")
    | ."conditions" |= walk(
        if type == "object" and has("condition") then
            if ."condition" == "fabric:not" then
                ."condition" = "forge:not"
            end
            | if ."condition" == "fabric:and" then
                ."condition" = "forge:and"
            end
            | if ."condition" == "fabric:or" then
                ."condition" = "forge:or"
            end
            | if ."condition" == "fabric:all_mods_loaded" then
                ."condition" = "forge:and"
                | ."values" |= [.[] | {
                    "type": "forge:mod_loaded",
                    "modid": .
                }]
            end
            | if ."condition" == "fabric:any_mod_loaded" then
                ."condition" = "forge:or"
                | ."values" |= [.[] | {
                    "type": "forge:mod_loaded",
                    "modid": .
                }]
            end
            | if ."condition" == "fabric:tags_populated" then
                ."condition" = "forge:not"
                | ."values" |= {
                    "type": "forge:or",
                    "values": [.[] | {
                        "type": "forge:tag_empty",
                        "tag": .
                    }]
                }
            end
            | ."type" = ."condition" | del(."condition")
        end
    )
end
| walk(
    if type == "object" and has("fabric:type") then
        . #TODO: custom ingredients fix
        | ."type" = ."fabric:type" | del(."fabric:type")
        | if ."type" == "fabric:any" then
            . = ."ingredients"
        end
    end
)
| if has("type") and ."type" == "farmersdelight:cutting" then
    if has("sound") and ."sound" == "minecraft:item.axe.strip" then
        fdCutting("axe_strip")
    else
        fdCutting("axe_dig")
    end
end
| walk(
    if type == "object" and has("tag") and (."tag" | startswith("c:")) then
        ."tag" |= sub("c:"; "forge:")
    end
)
