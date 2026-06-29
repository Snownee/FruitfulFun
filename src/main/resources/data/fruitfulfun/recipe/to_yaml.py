import os
import json
import yaml

def convert_json_to_yaml(root_dir):
    # 遍历当前目录及其所有子目录
    for root, dirs, files in os.walk(root_dir):
        for file in files:
            # 检查文件扩展名
            if file.endswith('.json'):
                json_path = os.path.join(root, file)
                # 生成新的 yaml 文件路径（替换扩展名）
                yaml_path = os.path.splitext(json_path)[0] + '.yaml'

                try:
                    # 读取 JSON
                    with open(json_path, 'r', encoding='utf-8') as f:
                        data = json.load(f)

                    # 写入 YAML
                    with open(yaml_path, 'w', encoding='utf-8') as f:
                        # allow_unicode=True 确保中文不被转义成 \uXXXX
                        yaml.safe_dump(data, f, default_flow_style=False, allow_unicode=True)

                    print(f"成功: {json_path} -> {yaml_path}")

                except Exception as e:
                    print(f"错误: 转换 {json_path} 时出错: {e}")

if __name__ == "__main__":
    convert_json_to_yaml("hybrid")
    convert_json_to_yaml("lychee")
    convert_json_to_yaml("ritual")
    print("\n转换任务已完成。")