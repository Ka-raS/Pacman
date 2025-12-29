# Pacman

| ![Main Menu](/screenshots/MainMenu.png) | ![Playing](    /screenshots/Playing.png)    |
|:---------------------------------------:|:-------------------------------------------:|
| ![Paused](   /screenshots/Paused.png)   | ![High Scores](/screenshots/HighScores.png) |

## Gameplay

- Four different Screens: Main Menu, Playing, Paused, High Scores
- Blinky, Pinky, Inky and Clyde each with their own chasing behavior
- Ghosts have Normal Sprite, Scared Sprite and Dead Sprite
- Sound effect when eating Pellets, Ghosts, Powerup, when Win or Lose...
- High scores are saved and displayed the next time the game is launched 
- Resizable Window

## How to Run

### From Latest Release: 

```bash
curl -LO https://github.com/Ka-raS/Pacman/releases/download/v1.1.0/pacman-1.1.0.jar
java -Dsun.java2d.opengl=true -jar pacman-1.1.0.jar
```

### Build And Run From Source

#### Requirements

- [JDK 25](https://www.oracle.com/java/technologies/javase/jdk25-archive-downloads.html)
- [Maven 3.9.12](https://maven.apache.org/install.html) - Build Automation Tool

```bash
git clone https://github.com/Ka-raS/Pacman.git
cd Pacman
mvn package
java -Dsun.java2d.opengl=true -jar target/pacman-1.1.0.jar
```

> Note: `-Dsun.java2d.opengl=true` flag enables GPU acceleration, see more [here](https://docs.oracle.com/en/java/javase/22/troubleshoot/java-2d-pipeline-rendering-and-properties.html)

## Controls

- Movement: W A S D or ← ↑ → ↓  
- Pause: Esc
- Select: Enter
