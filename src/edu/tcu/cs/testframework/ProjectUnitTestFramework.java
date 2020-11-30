package edu.tcu.cs.testframework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProjectUnitTestFramework {
	
	// Used to store the path of all classes
    private static List<String> testCaseClassNames = new ArrayList<String>();
    private static final List<TestCase> testCases = new ArrayList<>();

    public static void register(TestCase testCase) {
        testCases.add(testCase);
    }
    
    public static void main(String[] args) {
    	
        // 1. Find all client classes
        // Base package should be stored in a properties file if used in production
        scanPackage("client");        
        // 2. Instantiate those TestCase classes and populate testCases list
        createTestCases();        
        // 3. Execute all test cases
        for (TestCase testCase : testCases) {
            testCase.start(testCase);
        }
    }
    
    /**
     * Find the names of classes given a package
     * Note: https://stackoverflow.com/questions/6608795/what-is-the-difference-between-class-getresource-and-classloader-getresource
     * @param basePackage the package name
     */
    private static void scanPackage(String basePackage) {
    	
        URL url = ProjectUnitTestFramework.class.getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        String fileStr = url.getFile();
        File file = new File(fileStr);
        // Get all file names under this directory
        String[] filePaths = file.list();        
        for (String path : filePaths) {        	
            // Use absolute path to create a file
            File f = new File(fileStr + "/" + path);
            if (f.isDirectory()) {
            	// Recurse until file is found
                scanPackage(basePackage + "." + path);
            } else {
                testCaseClassNames.add(basePackage + "." + f.getName());
            }
        }
    }
    
    private static void createTestCases() {
    	
        if (testCaseClassNames.size() <= 0) {
            System.out.println("scan failed... make sure base package is correct");
            return;
        } else {
            for (String className : testCaseClassNames) {
                // Create an instance
                String cn = className.replaceAll(".class", "");
                // Load this class
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(cn);                    
                    // If class extends TestCase and is not the TestCase abstract class
                    if (TestCase.class.isAssignableFrom(clazz) && clazz != TestCase.class) {
                        Object object = clazz.getConstructor().newInstance();
                        register((TestCase) object);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
