from firebase import firebase
firebase = firebase.FirebaseApplication("https://espgame-2bc2d.firebaseio.com/",None)

for i in range(15):
	firebase.put('Selected_Cards',str(i),{str('0ne'):-1})
