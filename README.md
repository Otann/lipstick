# lipstick :lipstick:

Lipstick is a UI for your [Swagger][swagger] API definitions
 
![Screenshot](https://otann.github.io/lipstick/lipstick_demo.png)
 

Check demo at [http://otann.github.com/lipstick/progress](http://otann.github.com/lipstick/progress)

## Main Features

- Provide single js+html bundle
- Close to plain-text data presentation, ready to be copy-pasted to chat or email
- Non-obstructive client to get live data from API
- Optional configuration through `/lipstick.yaml` file

## Development Mode

### Compile css:

Compile css file once.

    lein less once

Automatically recompile css file on change.

    lein less auto

### Run application:

    lein clean
    lein figwheel dev

Figwheel will automatically push cljs changes to the browser.
Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build

To compile clojurescript to javascript:

    lein clean
    lein cljsbuild once min
    
    
[swagger]: http://swagger.io/    
