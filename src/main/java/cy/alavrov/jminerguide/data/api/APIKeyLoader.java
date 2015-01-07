package cy.alavrov.jminerguide.data.api;

import cy.alavrov.jminerguide.data.character.APIKey;

/**
 *
 * @author alavrov
 */
public class APIKeyLoader implements Runnable{
    private final APIKey key;
    private final IKeyLoadingResultReceiver receiver;
    
    public APIKeyLoader(APIKey key, IKeyLoadingResultReceiver receiver) {
        this.key = key;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch  (InterruptedException e) {
            e.printStackTrace();
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                receiver.loadingDone(false, "No, just no", key);
            }
        });
    }
    
}
