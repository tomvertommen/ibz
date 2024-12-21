package ibz;

public class Bundle {
	
	private Result previousResult;
	private Result newResult;
	
	public Bundle(Result previousResult, Result newResult) {
		this.previousResult = previousResult;
		this.newResult = newResult;
	}

	public Result getPreviousResult() {
		return previousResult;
	}
	
	public void setPreviousResult(Result previousResult) {
		this.previousResult = previousResult;
	}
	
	public Result getNewResult() {
		return newResult;
	}
	
	public void setNewResult(Result newResult) {
		this.newResult = newResult;
	}

}
