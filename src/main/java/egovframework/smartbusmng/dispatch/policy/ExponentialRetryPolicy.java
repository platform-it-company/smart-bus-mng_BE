package egovframework.smartbusmng.dispatch.policy;

public class ExponentialRetryPolicy implements RetryPolicy {

	private final int maxRetry;
	private final long baseSec;
	private final long maxSec;
	
	public ExponentialRetryPolicy(int maxRetry, long baseSec, long maxSec) {
		this.maxRetry = maxRetry;
		this.baseSec = baseSec;
		this.maxSec = maxSec;
	}
	
	@Override 
	public int maxRetry() {
		return maxRetry;
	}
	
	@Override
	public long nextDelaySec(int retryCntAfterIncrement) {
		// 1,2,3, .. => base*2^(n-1)
		long d = baseSec * (1L << Math.max(0,  retryCntAfterIncrement -1));
		return Math.min(d, maxSec);
	}
}
