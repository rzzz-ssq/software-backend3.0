import pandas as pd
from sqlalchemy import create_engine
import psycopg2
import sys
import json

def get_fill_rate(database, user, password, host, port, table_name):
    # 数据库配置：请根据你的数据库信息进行修改
    conn = psycopg2.connect(database=database, user=user, password=password, host=host, port=port,
                            options='-c client_encoding=utf-8')
    cursor = conn.cursor()

    # 执行SQL查询，获取表数据
    query = f'SELECT * FROM {table_name};'
    cursor.execute(query)
    rows = cursor.fetchall()
    # 获取字段名
    field_names = [desc[0] for desc in cursor.description]

    # 将查询结果转换为DataFrame对象
    df = pd.DataFrame(rows, columns=field_names)

    # 将空字符串替换为 NaN
    df.replace('', pd.NA, inplace=True)

    # 关闭数据库连接
    cursor.close()
    conn.close()

    # 计算缺失值统计信息
    missing_counts = df.isnull().sum()  # 缺失值计数
    total_counts = df.shape[0]  # 总行数
    missing_percentages = 100 - (missing_counts / total_counts) * 100  # 缺失比例

    # 将结果整合到DataFrame中
    missing_stats = pd.DataFrame({
        'column_name': df.columns,
        'missing_percentage': missing_percentages
    })
    return missing_stats

if __name__ == '__main__':
    table_name = sys.argv[1]
    stats = get_fill_rate("software4V2", "pg", "111111", "10.16.48.219", "5432", table_name)
    data = {
        'column_name': stats['column_name'].tolist(),
        'miss_rate': stats['missing_percentage'].tolist()
    }
    json_string = json.dumps(data, ensure_ascii=False).encode('utf-8')
    sys.stdout.buffer.write(json_string)
