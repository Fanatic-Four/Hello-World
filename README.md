# Hello-World
##November 7 - HackHolyoke - 2015
##First Place Winner
##Team Members: Tommy Nguyen, Kayla Nguyen, Calvin Mei, Aint Myat Noe

###Ever wanted real-life subtitles when hanging out with your multi-lingual friends or relatives? Now you can!

##Inspiration
Communication has always been a part of everyones' lives, no matter who you are, or where you come from. However, even in the year 2015, we still have an extremely difficult time communicating with the rest of the world. This app was inspired by the desire to be able to communicate with people who we might otherwise find impossible to communicate with.

##What it does
Hello-World is a mobile application that detects your speech and displays subtitles over a live stream. The app opens up to a live video stream and will detects when someone is speaking. As soon as it detects speech, it translates that speech to the subtitles in the language of your choice. In addition, we used sentiment analysis on the content of the speech to determine whether the speech was positive, negative, or neutral.

##How we built it
We used Android Studio, Google Speech Detection, and Microsoft Translator services. We utilized Indico API's for the sentiment analysis.

##Challenges we ran into
One big challenge we ran into was getting the translation to work. After we had gotten the speech recognition, we needed to translate that data into the desired language, but doing so proved difficult because of the complexity of some of the API's (Google...) we've tried to use. We did eventually find a solution with a library based on Microsoft Bing Translator. Also, our speech recognition currently supports only English.

##Accomplishments that we are proud of
After having four people work more hours than I can count on the same problem, we found a solution to translating the speech. 

##What we learned
We learned about how to send HTTP GET requests from android, various Android development, how to use Indico API's, and how difficult some API's can be to use.

##What's next for Hello-World
-Support for speech detection of more languages 
-Support for translation to more language-subtitles 
-Continuous detection of speech. 
-Porting this app onto other platforms: Oculus, AR, etc.
