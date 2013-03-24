#Dumbledroid

![Dumbledroid icon](https://dl.dropbox.com/u/5135185/blog/dumbledroid-icon.png)

Android + webservice = magic!

Download the [example app on Google Play](https://play.google.com/store/apps/details?id=io.leocad.dumbledoreexample) to see it in action!

##Current version
1.0 (20130227). Download the binary here: [dumbledroid-1.0.jar](https://dl.dropbox.com/u/5135185/blog/dumbledroid-1.0.jar).

*Eclipse plugin*: install it using the update site: [http://leocad.io/dumbledroidplugin/](http://leocad.io/dumbledroidplugin/). Learn how to install the plugin [here](https://github.com/leocadiotine/Dumbledroid/wiki/Installing_Eclipse_plugin).

##Purpose
Dumbledroid is a framework that enables integration between an Android app and a RESTful server using magic. ~~Actually, it's not real magic. I'm kidding.~~

Using Dumbledroid, the developer doesn't have to write parsers for JSON or XML documents from a web service. It maps the document nodes to the class fields and does this automagically!

Dumbledroid also does automatic caching in memory and in disk. *Presto!*

##Origin of the name
Dumbledroid was named after the wizard [Dumbledore](https://en.wikipedia.org/wiki/Dumbledore), a ~~blatant copy of Gandalf~~ major character of the *Harry Potter* series.

##How it works
This framework gets the names of the JSON/XML nodes and uses [reflection](http://docs.oracle.com/javase/tutorial/reflect/index.html) to find out the names of the class fields. Then, it maps the value of the first to the second.

It also adds non-intrusive memory caching using [SoftReferences](http://docs.oracle.com/javase/1.4.2/docs/api/java/lang/ref/SoftReference.html), i.e., when the garbage collector needs the memory, it is freed. The disk cache works by saving the data to the user's SD card.

Furthermore: its connection with the web service is smart. Dumbledroid only opens a connection and checks the headers. If the web service's version wasn't modified since the last time Dumbledroid downloaded it, the framework will use the cached version.

##Usage
You can find a whole working example on the `DumbledoreExample` folder of this repository. It's a working Android project that uses the Dumbledore framework. But for the purposes of this manual, following are step by step instructions.

###Step 1: Add the framework to your Android project
You can do this in two different ways: importing the Dumbledroid project on Eclipse and [referencing it as a library project](https://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject) or simply copying the binary file (.jar) to the `libs` directory of your Android project. You can find the latest binary on the "Current Version" paragraph of this document.

###Step 2: Add the permissions to your AndroidManifest.xml
Dumbledroid requires the following permissions to work:

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

The `WRITE_EXTERNAL_STORAGE` is optional and is used by the disk cache.

###Step 3: Generate (or write) your classes
Dumbledroid has its own Eclipse plugin to generate the classes from an URL. You can find instructions on how to install it [here](https://github.com/leocadiotine/Dumbledroid/wiki/Installing_Eclipse_plugin).

After installing it, go to the `File > New > Other…` menu and select `Dumbledore Model file`.

![New Dumbledroid Model file](https://dl.dropbox.com/u/5135185/blog/dumbledroid-file-new.png)

Then, just paste the URL for the JSON or XML, tweak the settings and *aparecium*! You're done!

![Dumbledroid input URL](https://dl.dropbox.com/u/5135185/blog/dumbledroid-input-url.png)

If you want to manually write your classes, you'll need to create the fields that correspond to your JSON or XML document.

For example, if you have a JSON like this:

    {
    	"name": "Luke",
    	"surname": "Skywalker",
    	"age": 18,
    	"isFromDarkSide": false,
    	"averageShipSpeed": 138.46
    }

You'll need to write a class like this:

    public class Jedi extends AbstractModel {
        
    	private String name;
    	private String surname;
    	private int age;
    	private boolean isFromDarkSide;
    	private double averageShipSpeed;
    
    	public Jedi() {
    		super("YOUR_JSON_URL");
    	}
    	
    	@Override
    	protected DataType getDataType() {
    		return DataType.JSON;
    	}
    }

The names of the class fields should be exactly the names of the JSON nodes. The class must extend `AbstractModel`, provide an URL in the constructor and return `DataType.JSON` or `DataType.XML` in the `getDataType()` method.

Feel free to make the fields public or to write getters and setters. Dumbledroid doesn't care about that.

###Step 4: Call Dumbledroid from within your Activity (or Context)
After that, you'll just need to do this:

    Jedi jedi = new Jedi();
    jedi.load();

That's it. Your JSON is already parsed and mapped to the Jedi instance. If you enabled caching, Dumbledroid already took care of that for you. Simple, isn't it?

*Note: make sure to call `load()` from a background thread, or [your app can crash](https://developer.android.com/training/articles/perf-anr.html).*

##Learn more
You can find some interesting articles about Dumbledroid usage and code examples at the [Wiki page](https://github.com/leocadiotine/Dumbledroid/wiki/_pages).

##License
Dumbledroid source code is released under BSD 2-clause license. Check LICENSE file for more information.

If you use this code, I'd appreciate you refer my name (Leocadio Tiné) and the link to this project's page in your project's website or credits screen. Though you don't have any legal/contractual obligation to do so, just good karma.

##Logo credits
The logo was created using [Androidify](http://androidify.com/).

##Suggestions? Comments?
Pull requests are always welcome. So are donations :)

To find me, buzz at `me[at]leocad.io` or [follow me on Twitter](http://www.twitter.com/leocadiotine). To read interesting stuff, go to [my blog](http://blog.leocad.io).

Special thanks to [Luis Medeiros](https://github.com/lpmfilho) and [diegocarloslima](https://github.com/diegocarloslima) for the pull requests!