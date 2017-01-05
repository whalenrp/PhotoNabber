# PhotoNabber

PhotoNabber is a spiritual successor to [PhotoGrabber](http://photograbber.org/), which ceased development in April 2015. The app is very crude right now and basically only usable by developers, so let me know if you have the time/patience to help give this a nice fancy gui :)

## Running

First, set up your environment by adding a `build.properties` file with the following:

```
jdk.home.1.8=/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home
```

Make sure the value to the right of the equals sign points to your JDK installation.

Once that's done, start the app by running `ant run`. This will build the app and run it,
prompting you for your Facebook login credentials and then downloading all of the images
into the `out` directory. After executing, check there to find all of your photos
archived (including tagged images) for future reference.

## Privacy concerns?

PhotoNabber does not track or store any information about you in any way. All information transmitted by the app is only to and from Facebook itself.
