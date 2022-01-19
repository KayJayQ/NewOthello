# NewOthello
Still multiplayer Othello, but in Java implementation (and PLAYABLE). 

## Alpha 0.2
Switch language by menubar options
### Single player:
    to enable AI, click top-left selection on menubar to config AI.
    3 different levels represent the depth restriction of game tree search

    If you do not enable AI, you can place pieces by yourself in black/white order

### Multiplayer:
    To config multiplayer, you have to ensure there is one active server that
    be accessable to you and match player. Click python script in server folder
    to deploy it on your machine, and config your server IP address in all language
    text files under ./lang folder. IP address is located at the end of file.

    If server is on, click multiplayer to refresh the online player list. Once you refresh,
    your system username will be displayed to other players who is viewing the list. Then that
    player can choose to match you.

    Once both players are matched, the game will start. 

## Known Issues
    Unstable multiplayer game. Both players might be wait for each other's move and causes deadlock.
    Bug is not likely to reappear in local debug environment. Internet delay involves.
    