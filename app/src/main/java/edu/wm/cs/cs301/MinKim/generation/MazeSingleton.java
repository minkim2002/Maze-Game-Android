package edu.wm.cs.cs301.MinKim.generation;

public class MazeSingleton {
    private static MazeSingleton mazeSingleton = null;
    private static Maze maze;

    public MazeSingleton() {}

    public static MazeSingleton getMazeSingleton() {
        if (mazeSingleton == null) {
            synchronized (MazeSingleton.class) {
                mazeSingleton = new MazeSingleton();
            }
        }
        return mazeSingleton;
    }

    public Maze getMaze() {
        return maze;
    }

    public void setMaze(Maze mazeConfig) {
        maze = mazeConfig;
    }
}
