# Pacman

| ![Main Menu](/screenshots/MainMenu.png) | ![Playing](    /screenshots/Playing.png)    |
|:-------------------------------------------------------------:|:---------------------:|
| ![Paused](   /screenshots/Paused.png)   | ![High Scores](/screenshots/HighScores.png) |

## Gameplay

- Four different Screens: Main Menu, Playing, Paused, High Scores
- Blinky, Pinky, Inky and Clyde each with their own chasing behavior
- Ghosts have Normal Sprite, Scared Sprite and Dead Sprite
- Sound effect when eating Pellets, Ghosts, Powerup, when Win or Lose...
- High scores are saved and displayed the next time the game is launched 

## Controls

- Movement: W  A  S  D or ← ↑ → ↓  
- Pause: Esc
- Select: Enter

## Download Prebuilt

### Latest Release: 

https://github.com/Ka-raS/Pacman/releases/download/v1.0.0/pacman-1.0.0.jar

## Build And Run From Source

### Requirements

- [Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Maven 3.9](https://maven.apache.org/install.html) - Build Manager

```bash
git clone https://github.com/Ka-raS/Pacman.git
cd pacman
mvn package
java -jar target/pacman-1.0.0.jar
```