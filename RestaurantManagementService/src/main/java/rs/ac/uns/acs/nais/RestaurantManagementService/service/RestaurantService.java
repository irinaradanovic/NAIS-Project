package rs.ac.uns.acs.nais.RestaurantManagementService.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.RestaurantManagementService.dto.RestaurantDTO;
import rs.ac.uns.acs.nais.RestaurantManagementService.model.Restaurant;
import rs.ac.uns.acs.nais.RestaurantManagementService.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant create(RestaurantDTO dto) {
        Restaurant r = new Restaurant();
        r.setName(dto.getName());
        r.setAddress(dto.getAddress());
        r.setContact(dto.getContact());
        return restaurantRepository.save(r);
    }

    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant with id " + id + " not found"));
    }


    public Restaurant getByName(String name){
        return restaurantRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Restaurant with name " + name + " not found"));
    }

    public Restaurant update(Long id, RestaurantDTO updated) {
        Restaurant existing = getById(id);

        // ako se nista ne promeni, ostavi staro
        if (updated.getName() != null) {
            existing.setName(updated.getName());
        }
        if (updated.getAddress() != null) {
            existing.setAddress(updated.getAddress());
        }
        if (updated.getContact() != null) {
            existing.setContact(updated.getContact());
        }
        return restaurantRepository.save(existing);
    }

    public void delete(Long id) {
        Optional<Restaurant> rest = restaurantRepository.findById(id);
        if (rest.isEmpty()) {
            throw new RuntimeException("Restaurant with id " + id + " not found");
        }
        restaurantRepository.deleteById(id);
    }
}
