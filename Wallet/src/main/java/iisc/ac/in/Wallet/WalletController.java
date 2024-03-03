package iisc.ac.in.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.net.URL;


@RestController
@RequestMapping("/wallets")

public class WalletController {
    @Autowired
    private WalletRepository walletRepository;


    @GetMapping("/{user_id}")
    public ResponseEntity<Object> getWalletByUserId(@PathVariable Integer user_id) {
        Wallet wallet = walletRepository.findByUserId(user_id);
        if (wallet != null) {
            PseudoWallet wallet1;
            wallet1 = new PseudoWallet(wallet.getUserId(), wallet.getBalance());
            return ResponseEntity.ok(wallet1);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet not found for user: " + user_id);
        }
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<Object> updateWallet(@PathVariable Integer user_id, @RequestBody WalletUpdateRequest request) {
        Wallet wallet = walletRepository.findByUserId(user_id);
        if (wallet == null) {
            try {

                URL url = new URL("http://host.docker.internal:8080/users/"+ user_id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    wallet = new Wallet(user_id, 0);
                    walletRepository.save(wallet);
                }else if(responseCode == HttpURLConnection.HTTP_NOT_FOUND) {

                    return new ResponseEntity<>("No User with this ID", HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if ("debit".equals(request.getAction())) {
            if (wallet.getBalance() < request.getAmount()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance");
            }
            wallet.setBalance(wallet.getBalance() - request.getAmount());
        } else if ("credit".equals(request.getAction())) {
            wallet.setBalance(wallet.getBalance() + request.getAmount());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action");
        }

        walletRepository.save(wallet);
        return ResponseEntity.ok(wallet);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Object> deleteWallet(@PathVariable Integer user_id) {
        Wallet wallet = walletRepository.findByUserId(user_id);
        if (wallet != null) {
            walletRepository.delete(wallet);
            return ResponseEntity.ok("Wallet deleted successfully for user: " + user_id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet not found for user: " + user_id);
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteAllWallets() {
        walletRepository.deleteAll();
        return ResponseEntity.ok("All wallets deleted successfully");
    }


}