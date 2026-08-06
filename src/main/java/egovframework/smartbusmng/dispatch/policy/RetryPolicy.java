package egovframework.smartbusmng.dispatch.policy;

public interface RetryPolicy {
	int maxRetry();
	long nextDelaySec(int retryCntAfterIncrement);
}
