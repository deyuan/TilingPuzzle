# Tiling Puzzle Solver

## Description

This is a 2D polyomino tiling puzzle solver implemented in Java with GUI based on dancing links (DLX) algorithm. Beyond the recursive DLX algorithm, a loop version DLX algorithm is implemented, with some optimizations such as eliminating symmetric solutions and dealing with duplicated tiles.


## Input Puzzle Format

Each input puzzle is a text file, which uses space-separated character blocks to represent tiles and the puzzle board. Different characters represent different colors. The largest block will be chosen as the puzzle board.

Example 1: pentominoes3x20.txt
```
                            #
  ###        #    #     #   #
   #   # #   #    ##   ###  ##
   #   ###   ###   ##   #   #

       #  #        #
   ##  #  #   ##   #  ##
  ##   #  #   ##  ##   #
   #   #  ##  #   #    ##
       #

  ####################
  ####################
  ####################
```

Example 2: checkerboard.txt
```
         O     OXOXO         OX
     X   XO        X  XO     XO          XOXOXOXO
     O    XO           X     O     X     OXOXOXOX
     X                       X     O     XOXOXOXO
           X   O     XO         OXOX     OXOXOXOX
    XOXO   O   X     OX                  XOXOXOXO
    O      X   OXO    O    X             OXOXOXOX
    X      O          X    O    OX       XOXOXOXO
           XO         O    X    XO       OXOXOXOX
                          XO     X
                          O
```


## Graphical User Interface

This solver allows user to configure the tile rotation, tile reflection, symmetric solution elimination, and can visually demonstrate solutions and single search steps.

![ScreenShot](https://raw.githubusercontent.com/Deyuan/TilingPuzzle/master/screenshots/screenshot_hexomino.png)

