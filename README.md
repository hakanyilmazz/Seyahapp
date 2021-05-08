# Seyahapp
 Travel, Chatting And Crypto Currencies

### Application
<img src="https://github.com/hakanyilmazz/Seyahapp/blob/main/ScreenShots/seyahapp.gif" height="500"></br>

If you want to run this project, </br>
1. You need to google maps api for in debug folder google_maps_api.xml file.</br>
2. Also you need to google_services.json file for firebase in app folder.

### Message Encryption Algorithm
1. Generate unique public id. </br>
2. Get public id numbers' average. </br>
3. Generate a random number from 0 to 100. </br>
4. Xor key = random number + public id's average. </br>
5. Generate sha256 for message verification . </br>
6. Get encrypted message with xor key and xor algorithm. </br>
7. Send encrypted message + sha256 + public id
8. Repeat for every writed message. </br>

### Message Decryption Algorithm
1. Get public id numbers' average. </br>
2. Try decryption with xor algorithm and average + 0-100 numbers. </br>
3. If my decrypted message sha256 == original message sha256 show message, Else Try again or show Error message. </br>

### Advantages
1. Random encryption for each message </br>
2. Is message equals original message? Verification control. So it can be noticed if the message changes while en route. </br>
3. I think it is necessary to know the algorithm to decrypt the message. Because this algorithm uses random encryption. </br>
