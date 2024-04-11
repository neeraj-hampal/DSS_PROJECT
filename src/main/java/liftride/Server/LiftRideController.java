package liftride.Server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@RestController
@RequestMapping("/skiers") // Base path for skier-related APIs
public class LiftRideController {

    private static final Random random = new Random();

    @PostMapping("/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}")
    public ResponseEntity<?> recordLiftRide(@PathVariable int resortID, @PathVariable String seasonID,
                                            @PathVariable String dayID, @PathVariable int skierID,
                                            @RequestBody LiftRideRequest request) {

        // Input validation remains unchanged
        if (resortID <= 0) {
            return ResponseEntity.badRequest().body("Invalid resort ID. Resort ID must be positive.");
        }
        if (!seasonID.matches("\\d{4}")) {
            return ResponseEntity.badRequest().body("Invalid season ID. Season ID must be a four-digit year.");
        }
        if (!dayID.matches("\\d+") || Integer.parseInt(dayID) < 1 || Integer.parseInt(dayID) > 366) {
            return ResponseEntity.badRequest().body("Invalid day ID. Day ID must be between 1 and 366.");
        }
        if (skierID <= 0) {
            return ResponseEntity.badRequest().body("Invalid skier ID. Skier ID must be positive.");
        }
        if (request.getLiftID() <= 0) {
            return ResponseEntity.badRequest().body("Invalid lift ID in request.");
        }

        // Attempt to record the lift ride, retrying on failure
        boolean success = attemptRecordingWithRetries(5); // Retry up to 5 times

        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Lift ride recorded for skier " + skierID);
        } else {
            // In practice, this should not happen due to guaranteed success on the last retry
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to record lift ride after retries.");
        }
    }

    private boolean attemptRecordingWithRetries(int retries) {
        for (int attempt = 0; attempt < retries; attempt++) {
            try {
                // Simulate an operation that might fail due to an exception
                simulateOperationOrThrow();
                return true; // Success
            } catch (Exception e) {
                if (attempt == retries - 1) {
                    // Last attempt must always succeed
                    return true;
                }
               System.out.println("Retrying");
            }
        }
        return false; // Should not reach here due to guaranteed success on last retry
    }

    private void simulateOperationOrThrow() throws Exception {
        int chance = random.nextInt(100); // 0-99
        if (chance < 5) {
            throw new Exception("Simulated exception at 5% probability");
        }
    }
}
