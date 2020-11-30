package edu.tcu.cs.testframework;

/**
 * Simple unit test framework
 */
public abstract class TestCase {
	
	/**
     * This method is to be implemented by any class that extends this class
     * @return
     */
	public abstract boolean runTest();
	
	public void start(TestCase testCase) {
        if (runTest()) {
            System.out.println(testCase + ": Test passed.\n\n========================================\n");
        } else {
            System.out.println(testCase + ": Test failed.\n\n========================================\n");
        }
    }
	
	@Override
	public String toString() {
		return super.toString().replaceAll("@.*$", "");
	}
}
