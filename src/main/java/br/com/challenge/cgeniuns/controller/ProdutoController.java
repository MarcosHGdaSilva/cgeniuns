package br.com.challenge.cgeniuns.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.challenge.cgeniuns.model.Produto;
import br.com.challenge.cgeniuns.repository.HistoricoRepository;
import br.com.challenge.cgeniuns.repository.ProdutoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("produto")
@Slf4j
@Tag(name = "produtos", description = "Endpoint relacionado com produtos")
public class ProdutoController {
    @Autowired
    ProdutoRepository produtoRepository;
    @Autowired
    HistoricoRepository historicoRepository;

    @GetMapping
    @Operation(summary = "Lista todos os produtos cadastrados no sistema.", description = "Endpoint que retorna um array de objetos do tipo produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Page<Produto> index(
        @RequestParam(required = false) String cpf,
        @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable
    ){
        if (cpf != null){
            return produtoRepository.findByCpf(cpf, pageable);
        }
        return  produtoRepository.findAll(pageable);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Cria um novo produto.", description = "Endpoint para criar um novo produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação do produto"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Produto create(@RequestBody Produto produto){
        log.info("cadastrando produto: {}", produto);
        return  produtoRepository.save(produto);
    }

    @GetMapping("{id}")
    @Operation(summary = "Lista todos os produtos cadastrados no sistema.", description = "Endpoint que retorna um objetos do tipo produto com um id informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Produto> get(@PathVariable Long id){
        log.info("Buscar por id: {}", id);
        return  produtoRepository
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Deleta um produto pelo ID.", description = "Endpoint que deleta um produto com um ID informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public void destroy (@PathVariable Long id){
        log.info("Apagando id {}", id);

        verificarExistencia(id);
        produtoRepository.deleteById(id);
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualiza um produto pelo ID.", description = "Endpoint que atualiza um produto com um ID informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação do produto"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public  Produto update(@PathVariable Long id, @RequestBody Produto produto){
        log.info("Atualizando o cadastro do id={} para {}", id, produto);

        verificarExistencia(id);
        produto.setId(id);
        return produtoRepository.save(produto);
    }

    private void verificarExistencia(Long id){
        produtoRepository.
        findById(id)
        .orElseThrow(
            ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "id não encontrado")
        );
    }
}
