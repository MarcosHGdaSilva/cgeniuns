package br.com.challenge.cgeniuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.com.challenge.cgeniuns.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT cliente FROM Cliente cliente WHERE cliente.cpf_cliente = :cpf_cliente")
    Cliente findByCpf_cliente(@Param("cpf_cliente") String cpf_cliente);
    @Modifying
    @Transactional
    @Query("DELETE FROM Cliente cliente WHERE cliente.cpf_cliente = :cpf_cliente")
    void deleteByCpf_cliente(@Param("cpf_cliente") String cpf_cliente);

}
