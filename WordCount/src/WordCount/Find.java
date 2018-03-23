package WordCount;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;
import java.util.*;

// The class that uses glob pattern to match path recursively,
// get more information in (https://docs.oracle.com/javase/tutorial/essential/io/find.html)
public class Find {

	// Used to record every file whose path matches the pattern
	static ArrayList<String> strLst = new ArrayList<String>();
	
    public static class Finder
        extends SimpleFileVisitor<Path> 
    {

        private final PathMatcher matcher;

        Finder(String pattern) 
        {
            matcher = FileSystems.getDefault()
                    .getPathMatcher("glob:" + pattern);
        }

        // Compares the glob pattern against
        // the file or directory name.
        void find(Path file) 
        {
            Path name = file.getFileName();
            if (name != null && matcher.matches(name)) 
            {
                strLst.add(file.toString());
            }
        }

        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile(Path file,
                BasicFileAttributes attrs) 
        {
            find(file);
            return CONTINUE;
        }

        // Invoke the pattern matching
        // method on each directory.
        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) 
        {
            find(dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                IOException exc) 
        {
            System.err.println(exc);
            return CONTINUE;
        }
    }

    // The method called by Exec Class
    public static ArrayList<String> getFileNames(String totalPath)
        throws IOException 
    {
    	String dir = new String(), pattern = new String();
    	
    	int splitInd = totalPath.lastIndexOf("\\");
    
    	// If relative path is used, add ".\" as prefix
    	if(splitInd == -1)
    	{
    		dir = ".\\";
    		pattern = totalPath;
    	}
    	else  // Separate the directory and pattern
    	{
    		dir = totalPath.substring(0, splitInd+1);
    		pattern = totalPath.substring(splitInd+1);
    	}
    	
        Path startingDir = Paths.get(dir);
        Finder finder = new Finder(pattern);
        Files.walkFileTree(startingDir, finder);

        return strLst;
    }
    //Uncomment the code below to execute unit testing
//    public static void main(String[] args)
//    {
//    	ArrayList<String> fileLst = new ArrayList<String>();
//    	try
//    	{
//    		fileLst = getFileNames("*");
//    	}
//    	catch (Exception e)
//    	{
//    		System.err.println(e);
//    	}
//    	for(String file: fileLst)
//    	{
//    		System.out.println(file);
//    	}
//    }
}