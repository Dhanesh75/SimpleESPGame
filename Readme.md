Simple ESP Game
===================================


Pre-requisites
--------------

- Android SDK v23
- Android Build Tools v23.0.2
- Android Support Repository v23.3.
- Android Studio

External Libraries used
-------------
- <a href = "https://github.com/novoda/merlin"> Merlin </a> for checking changes in Internet Connectivity
- FirebaseAPIs for realtime read/write operations

Getting Started
---------------

This app uses the Gradle build system. To build this project, use the
"gradle build" command and/or use "Import Project" in Android Studio.

Make sure you register the firebase before running.

Using The App
-------

Set your username

For a given primary Image (In the Center)
Select (Tap) on one of the 4 corresponding secondary images (At Bottom)

There will be 5 questions i.e. 5 primary images appearing on the screen one by one
Keep Selecting your secondary image accordingly

If another user(s) selects the same secondary image for a given primary image
Your score will be increased by one.

Note: If there are multiple users, all the users should tap on the same secondary image of a given primary image
to increase their score.

Scores are updated asynchronously so even if you are on a different question and your previously answered question matches 
with someone, you and them will get a score.

Note: Since this app uses firebase in the background, there can be some delay in getting values.
If the score count is not updating then kindly restart the app, It will refetch the values.

You can use your own firebase as backend.

Setting up your own Firebase
---------

In Android studio, goto tools -> Firebase -> select Realtime database
Here complete the process of "connect your app to firebase" and "add realtime database to your app"
Enter Google's Id and Password (android studio will use it to connect to your firebase)

A browser pop up will open, allow the connection
Make a new realtime database.

Firebase Structure
----------

In your root make only one child (Array of size 15) and name it as "Selected_Cards"

Initialize your Array elements with a hashmap which will have key as Username and value as 
the selected Secondary choice if faced with that index's question.

Give a dummy entry of a dummy username and "-1" as value to every array element. 
Run the FirebaseResetter.py script to quickly reset all the values
Change the url of the firebase if you have a different one.

Screenshots of Firebase Architecture
---------

<img src = "Images/Screenshot (627).png" height = "500"/>
<img src = "Images/Screenshot (628).png" height = "500"/>




