import MySQLdb as sqldb
import pdb
passwd = raw_input("enter the mysql root password:")
db = sqldb.connect(host='localhost', user='root',passwd=passwd, db='test')
try:
    cursor = db.cursor()
    #cursor.execute("SELECT * FROM Complete WHERE MacAddress='1c:aa:07:17:82:d0'")
    #print (cursor.execute("UPDATE Complete SET Connections= %s WHERE MacAddress = %s",("241", "1c:aa:07:17:82:d0")))
    #db.commit()

    cursor.execute("SELECT MacAddress FROM Complete")
    rows = cursor.fetchall()
    pdb.set_trace()
    for r in rows:
        print (r)
    cursor.close()
except:
    print ("fetching from database was unsuccesful")
