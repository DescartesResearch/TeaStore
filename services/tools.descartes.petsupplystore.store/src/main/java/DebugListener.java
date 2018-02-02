import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class DebugListener {

	Logger logger = Logger.getLogger(DebugListener.class);
	
    @Before("execution(void *..lambda*(..))")
    public void logButtonClickLambda(JoinPoint thisJoinPoint) {
    	logger.warn("Caught button click (lambda): " + thisJoinPoint);
    }
    
}
