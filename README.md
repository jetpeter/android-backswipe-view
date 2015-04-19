# android-backswipe-view
Library module that allows simple implementation of back swipe navigation with Fragments

I pulled this module out of the original implementation of OpenReddit.  It does not follow Material design patterns, and is definitely not an android pattern.  That said I found this a really interesting proof on concept.  

Some things to note that went into the design are: 
  Fragments are hidden when not on the content pane. This allows you to not have to set a background for your fragments which will dramatically reduce overdraw for apps with multiple fragments in the stack.
  Fragments cannot simply be replaced since the recreation time of a fragment is not fast enough to provide a smooth animation.  Not being able to put the fragment in the "removed state" by replacing is poses a potential "memory leak" type problem.  Something that may be worth future exploration is removing the fragments that are 2 spaces behind the current content fragment. This solution may not work correctly with the current fragment system or may rely heavily on clever use of tags.

## Usage

Import the backswipeview module to your project, or add the files BackSwipeLayout.java and BackSwipeManager.java to your project.

