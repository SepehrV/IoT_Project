import numpy as np
import MySQLdb as sqldb
import time

passwd = raw_input('please enter mysql root password: ')
db = sqldb.connect(host='localhost', user='root',passwd= passwd, db='test')
def gen_connection(Min=0, Max=50):
    try:
        return np.random.randint(Min,Max)
    except e:
        print ("Min should be smaller than Max")

def update_connections():
    try:
        cursor = db.cursor()
        cursor.execute("SELECT MacAddress FROM Complete")

        rows = cursor.fetchall()
        for r in rows:
            cursor.execute("UPDATE Complete SET Connections= %s WHERE MacAddress = %s",(str(gen_connection()), r[0]))
        db.commit()
        cursor.close()
    except:
        print ("fetching from database was unsuccesful")


def main():
    while True:
        update_connections()
        time.sleep(60)

if __name__ == "__main__":
    main()
