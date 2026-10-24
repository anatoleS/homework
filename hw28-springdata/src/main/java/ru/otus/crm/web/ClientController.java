package ru.otus.crm.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.crm.model.Client;
import ru.otus.crm.repository.ClientRepository;
import ru.otus.crm.repository.AddressRepository;
import ru.otus.crm.repository.PhoneRepository;

@Controller
public class ClientController {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;

    public ClientController(ClientRepository clientRepository, AddressRepository addressRepository, PhoneRepository phoneRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.phoneRepository = phoneRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/clients")
    public String clients(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        return "clients";
    }

    @PostMapping("/clients")
    public String createClient(@RequestParam String name) {
        if (name != null && !name.isBlank()) {
            Client client = new Client(name);
            clientRepository.save(client);
        }
        return "redirect:/clients";
    }
}
