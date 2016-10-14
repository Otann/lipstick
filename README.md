# lipstick :lipstick: 💄

This projects aims to build basic and clean visualization of
swagger definitions.

Current progress is shown at [http://otann.github.com/lipstick/progress](http://otann.github.com/lipstick/progress)

Objective:
- single js+html file
- copy-pastable representation of data structures
- outline with endpoints
- "try" button with sane output (status, headers, explore body)
- auth support (bearer module)
- include examples for friboo, play, spring-boot

Inner projects:
- Data Explorer with collapsible structure
- Visual HTTP client
- Bearer configuration

Future scope:
- search across document
- API aggregator (dynamic sources, search?)
- other auth options (declare intention to accept PR)

Out of scope decisions:
- configuration (everything should be in /swagger.yaml)

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
