

Checkers
version 1.0.0

A standard checkers game written in java, which is designed to be played with two people. The code has been setup for
future implementation of a single player AI system, but it has yet to be implemented. This release is fully functional in every
other aspect. The game has two modes, described below, optional assistance, and optional rule enforcement. It also allows 
the ability to change themes, which this particular release only contains one theme. 


+ Hover Interaction
      A block will highlight with a special tone, default (Classic) with blue, when your cursor hovers over a usable block 
	     on the game board.
		 
+ Selection Highlight
      If a piece is selected, the piece will be highlighted based on selected theme, default (Classic) is yellow.
	  
+ Move Guide
      Activated with Options > [x] Show Guide
	  This feature will highlight any blocks with a movable piece with a special color based on the selected Theme.
	     The default (Classic) highlights the blocks with a green tone. If a movable piece is selected, a new tone is 
		 generated to the available blocks that selected piece can be moved to. The default (Classic) theme will highlight
		 these blocks with yellow tones.
		 
+ Force Jump
      Activated with Options > [x] Force Jumps
	  This feature will force an opponent to make a jump if it is available. This is useful for strategic purposes, and for
	     an alternative game-type. Some people play without enforcing this rule, which this accomodates by allowing 
		 the user to deactivate it. If this option is changed mid-game, it will reset the game.
		 
		 During the game type Reverse, this option cannot be disabled due to rules of the match.

+ Game Type: Regular
      Game > Mode > Regular
      Regular game type is bounded by the same rules in a common game of checkers. A piece can only move to an empty
	  adjacent block, or jump an adjacent opponent piece that has an empty adjacent block in the same direction. A piece can
	  only move in the direction opposite of their starting position unless they are kinged. If a piece is kinged, it can move in any
	  adjacent direction, if the block is empty. 
	  If a piece is jumped and forms another possible jump with the same piece, that jump must be made. This is a multi-jump.
	  Multi-jumps have no restrictions, and can continue until that piece can no longer jump an opponent, unless it is kinged. 
	  During a multi-jump, if a piece reaches the opposing side and is kinged, it can no longer continue a multi-jump even if
	  it is available. It can make the jump next turn, if still available.
	  In order to win, all of the opposing pieces must be elimated. The piece count is disabled in the Scoreboard, as well as
	  the player turn.

+ Game Type: Reverse
       Game > Mode > Reverse
	   Reverse game type has the same general rules as the Regular game type, but the goal is to purposely lose all your pieces.
	   If a player loses all of their pieces, they win the match. Due to this, Force Jump is automatically enabled, and cannot be 
	   disabled during this game-type. To prevent players from "running", the board will "collapse" during the game play when
	   the number of pieces minimize. This essentially shrinks the board, no smaller than 4x4. 

+ Undo
	   If you make a move you do not like, you can undo this with Game > Undo.
	   This will step back the board one move, and can be used until the board is at its original starting position. Please play fair.

+ Scoreboard
	   The scoreboard displays the current piece count for each player, as well as the current player's turn. The bar has two sides,
	   left being the bottom-board (player 2) and right being the top-board (player 1). Depending on the theme, the sides of the bar
	   adjusts colors to indicate the current turn. In the default theme (Classic), the current player's turn is highlighted in blue.
	   
+ Resize
      To keep a proper aspect ratio, the resizing has been restricted from the typical edge-dragging. To resize the board, you can 
	  scroll with a mouse. Up is to shrink, down is to stretch. Quick scrolls increase in large increments, while short, single scolls 
	  increase with higher accuracy. The size will not go any larger than your available display size, the board will auto rescale 
	  down to fit.
	  
+ Themes
	   The theme can be changed by Options > Themes > [Theme Name]
	   Any theme can be created and added to the available themes. Opening the "data.dat" file and adding the name of the theme
	   will add it to the list of themes under the options menu. To properly display these themes, it must be in its own folder matching
	   the same name, inside the "/themes/" parent folder. These images must be based on the original Classic scheme, as in the same
	   exact names will be required. If an image isn't properly named or not found, in some cases the will voluntarily exit. In non-essential
	   cases, it will continue on without having that particular effect/display.
	   
+ Background music can be changed by replacing the "background.wav" in the "/sounds/" folder with any .wav file.
------------------------------------------------------------------------------------

All code written by Timothy Sinard
All images created by Timothy Sinard
Default music from Mr. Lou; Dewfall Productions, originally titled "Storm in a Lake", 2005. No infringement intended.
