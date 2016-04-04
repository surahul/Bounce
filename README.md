# Bounce

A TouchListener capable of enabling ios style bounce and custom animations for ListView, ScrollView and RecyclerView overscroll effect

![](https://raw.githubusercontent.com/surahul/Bounce/master/example/images/launch_160p.gif)
![](https://raw.githubusercontent.com/surahul/Bounce/master/example/images/scroll_view_160p.gif)
![](https://raw.githubusercontent.com/surahul/Bounce/master/example/images/list_view_160p.gif)
![](https://raw.githubusercontent.com/surahul/Bounce/master/example/images/recycler_view_160p.gif)


## Demo App

### Download from Google Play

[![Get it on Google Play](https://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.rahul.bounce)

## Usage

1. Add `com.surahul:bounce:1.0.0` to your `dependencies` in `build.gradle`.
1. Set `BounceTouchListener` as onTouchListener to your scrollable view and that's it, your view will have a bounce effect on overscrolling.

You can even set an OnTranslateListener on touchListener which then gets translation callbacks for the bounce effect. This can be used to custom animate anything with overscrolling.  

See [the example app code](example/src/main/java/com/rahul/bounce/) for sample code.


## Apps that use this library

If you're using this library in your app and you'd like to list it here,  
please let me know via [email](mailto:denhelp1@gmail.com) or [pull requests](https://github.com/surahul/Bounce/pulls) or [issues](https://github.com/surahul/Bounce/issues).


## Contributions

Help me make this library better by contributing to the code. Any contributions are welcome!  


## Developed By

* [Rahul Verma](https://www.facebook.com/rahulverma199121) - [denhelp1@gmail.com](mailto:rahul.verma@gmail.com)


## Thanks



## License

```license
Copyright 2014 Bounce - Rahul Verma

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
