# process JSON files with jq from a folder to another folder recursively

import os
import subprocess

def process_json_file(input_file, output_file, jq_file):
    """Processes a single JSON file with jq and saves the output."""
    print(f"{input_file} -> {output_file}")
    try:
        os.makedirs(os.path.dirname(output_file), exist_ok=True)
        result = subprocess.run(
            ["jq", "-f", jq_file, input_file],
            check=True,  # Raise an exception if jq fails
            stdout=subprocess.PIPE,
            encoding="utf-8",
        )
        with open(output_file, "w") as f:
            f.write(result.stdout)
    except subprocess.CalledProcessError as e:
        print(f"Error processing {input_file}: {e}")

def process_json_files_recursively(input_folder, output_folder, jq_file):
    """Processes JSON files recursively in a folder."""
    for root, _, files in os.walk(input_folder):
        for file in files:
            if file.endswith(".json"):
                input_file = os.path.join(root, file)
                output_file = os.path.join(
                    output_folder, os.path.relpath(input_file, input_folder)
                )
                process_json_file(input_file, output_file, jq_file)

# Example usage:
input_folder = "fabric_jsons"
output_folder = "src"
jq_file = "fabric2forge.jq"  # Replace with your desired jq command

process_json_files_recursively(input_folder, output_folder, jq_file)
