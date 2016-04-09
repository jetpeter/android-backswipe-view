# android-backswipe-view
Library module that allows simple implementation of back swipe navigation with Fragments

I pulled this module out of the original implementation of OpenReddit.  It does not follow Material design patterns, and is definitely not an android pattern.  That said I found this a really interesting proof on concept.  

Some things to note that went into the design are: 

*  Fragments are hidden when not on the content pane. This allows you to not have to set a background for your fragments which will dramatically reduce overdraw for apps with multiple fragments in the stack.

*  Fragments cannot simply be replaced since the recreation time of a fragment is not fast enough to provide a smooth animation.  Not being able to put the fragment in the "removed state" by replacing is poses a potential "memory leak" type problem.  Something that may be worth future exploration is removing the fragments that are 2 spaces behind the current content fragment. This solution may not work correctly with the current fragment system or may rely heavily on clever use of tags.

## Usage

1. Import the backswipeview module to your project by adding the following to your project build.gradle in allprojects -> repositories.
```
    maven {
        url  "http://dl.bintray.com/jetpeter/maven"
    }
```
Then add `compile 'me.jefferey.backswipeview:backswipeview:1.0.0'` application dependincies

Alternitively add the files BackSwipeLayout.java and BackSwipeManager.java to your project.

2. Make the BackSwipeView class the root view in your activity layout.

3. In your activities onCreate method add  something like the following code 
```
    BackSwipeLayout backSwipeLayout = (BackSwipeLayout) findViewById(R.id.back_swipe_layout);
    FragmentManager fm = getSupportFragmentManager();
    // Create a BackSwipeManager to handle fragment transactions when you back swipe
    mBackSwipeManager = new BackSwipeManager(fm, backSwipeLayout);
    if (savedInstanceState == null) {
        // Only add the base fragment once since the fragment stack is restored from saved instance state.
        Fragment contentFragment = SampleFragment.newInstance();
        mBackSwipeManager.setBaseFragment(contentFragment);
    }
```
4.) Adding more fragments
```
// Adds a content fragment that will be back swipe enabled. 
mBackSwipeManager.addContentFragment(content);
```


