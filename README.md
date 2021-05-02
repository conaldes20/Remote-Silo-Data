# Real Time Grain Supply Details
This application was developed in 2018 and deployed in 3 grain silos of Federal Ministry of Agriculture, Strategic Grain Reserve Department. 

Before this application, the silo managers used to forward details (photos of receipts) of supplies through WhatsApp. This way, collation and analysis of records of supplies to silos were very rigorous and inaccurate.

The department wanted an application that can transmit data in real time to headquarters in Abuja and in a form that will analysis and evaluation easy.

There are weighbridges installed by Chinese company in the 3 silos. The weighbridges store data in Microsoft Access Database.

The program starts every time the computer starts running. It waits for a little while for the router to be ready. When the router is ready, it gets connected to Microsoft Access Database. It retrieves all current records, connects the remote server and sends the records.

If some records fail to be sent as a result of network failure, the next check on Microsoft Access Database for current records, another attempt will be made to send them.

If the router goes off, there will be a message on that and reconnection is made. In addition, log files are created to show more details when a problem occurs.

## Built With
-	Microsoft Access Database
-	MySQL (wampserver3.2.3_x86)
-	Java 8
-	netbeans-8.2-windows

## Get Started
-	Download jdk-8, netbeans-8.2-windows and wampserver3.2.3_x86 
-	Double click and follow instructions to install.
-	Create fmasgrdb database in wampserver with your own user and password. Select any host option.
-	Open RemoteServerData folder in netbeans, make changes and recompile.
-	Adjust C:\\Users\\CONALDES\\weighbridge\\logs\\ directory on your system. All files in logs folder are created if they do not exist. Put weight.accdb in msdb folder in under C:\\Users\\CONALDES\\weighbridge\\ . 
-	Drop RemoteData.bat in any location of your choice and create a shortcut to it.
-	Beside windows icon at bottom left corner (windows 10), type run and click Run App at the top. Then in the textbox, type shell:startup. In the folder that opens (start up), cut the shortcut created and put in this folder. That’s all.
-	This application is designed for remote, thus, your computer must be connected to internet.


