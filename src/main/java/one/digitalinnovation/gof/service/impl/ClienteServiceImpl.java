package one.digitalinnovation.gof.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 * 
 * @author Rodrigo Ramone
 */
@Service
public class ClienteServiceImpl implements ClienteService {

	// Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private static ClienteRepository clienteRepository;
	@Autowired
	private static EnderecoRepository enderecoRepository;
	@Autowired
	private static ViaCepService viaCepService;

	public Iterable<Cliente> buscarTodos(){
		return clienteRepository.findAll();
	};

	public Cliente buscarPorId(Long id){
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	};

	public void inserir(Cliente cliente){
		salvarClienteComCep(cliente);
	}


	public void atualizar(Long id, Cliente cliente){
		Optional<Cliente> clienteDb = clienteRepository.findById(id);
		if(clienteDb.isPresent()){
			salvarClienteComCep(cliente);
		}
	};

	public void deletar(Long id){
		clienteRepository.deleteById(id);
	};

	private static void salvarClienteComCep(Cliente cliente) {
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep)
				.orElseGet(() -> {
					Endereco novoEndereco = viaCepService.consultarCep(cep);
					enderecoRepository.save(novoEndereco);
					return novoEndereco;
				});
		cliente.setEndereco(endereco);
		clienteRepository.save(cliente);
	};

}
