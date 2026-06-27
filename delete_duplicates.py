import os
import re

# 1. 填入你的多行文本数据
text_data = open("delete_duplicates.txt", "r", encoding="utf-8").read()

def delete_files_from_text(text):
    # 使用正则表达式匹配每行开头 file '...' 里的路径
    # ^file\s+'([^']+)' 确保只抓取每行最开始的那个文件路径
    file_paths = re.findall(r"^file\s+'([^']+)'", text, re.MULTILINE)
    
    if not file_paths:
        print("❌ 未在文本中匹配到有效的文件路径。")
        return

    print(f"🔍 识别到 {len(file_paths)} 个待删除文件，开始处理...\n")
    
    for path in file_paths:
        # 标准化路径分隔符（防止 Windows 下的反斜杠引发问题）
        normalized_path = os.path.normpath(path)
        
        if os.path.exists(normalized_path):
            try:
                os.remove(normalized_path)
                print(f"✅ 已成功删除: {normalized_path}")
            except Exception as e:
                print(f"❌ 删除失败: {normalized_path}，原因: {e}")
        else:
            print(f"ℹ️ 文件不存在(跳过): {normalized_path}")

if __name__ == "__main__":
    # 执行删除
    delete_files_from_text(text_data)