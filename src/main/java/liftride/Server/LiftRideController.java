package liftride.Server;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/skiers") // Base path for skier-related APIs
public class LiftRideController {

 @PostMapping("/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}")
 public ResponseEntity<?> recordLiftRide(@PathVariable int resortID, @PathVariable String seasonID,
                                         @PathVariable String dayID, @PathVariable int skierID,
                                         @RequestBody LiftRideRequest request) {

     // Basic parameter validation
     if (resortID <= 0 || !seasonID.matches("\\d+") || !dayID.matches("\\d+") || skierID <= 0) {
         return ResponseEntity.badRequest().body("Invalid parameters");
     }
     
     
     // Dummy data response
     return ResponseEntity.status(HttpStatus.CREATED).body("Lift ride recorded for skier " + skierID);
 }
}
