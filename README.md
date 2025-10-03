# statemachine

### How to run async states

```yaml

id: test
start: start
states:
  - id: start
    action: startAction
    parallel: true # flag to run parallel
    join: finish   # join flag which state parallel finished before 
    next:
      - to: parallel1 # run parallel service concurrently
      - to: parallel2 # run parallel service concurrently

  - id: parallel1
    action: parallelAction1

  - id: parallel2
    action: parallelAction2

  - id: finish
    action: finishAction



```