package com.fithub.fithub_api.reserva.service;

import com.fithub.fithub_api.aula.entity.Aula;
import com.fithub.fithub_api.aula.service.AulaService;
import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.exception.ReservaException;
import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.reserva.repository.ReservaRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservaService implements ReservaIService {

    private final ReservaRepository reservaRepository;
    private final AulaService aulaService;

    @Transactional
    public Reserva criarReserva(Long idAula, Usuario usuarioLogado) {

        //procucar aula
        Aula aula = aulaService.buscarPorId(idAula);

        //verifica se o usuario ja reservou esta aula
        if(reservaRepository.existsByUsuarioIdAndAulaId(usuarioLogado.getId(), idAula)){
            throw new ReservaException("Usuario ja reservou esta aula");
        }
        int qtdVagas = reservaRepository.countByAulaId(idAula);
        // verifica se tem vagas disponiveis
        if(qtdVagas >= aula.getVagasTotais()){
            throw new ReservaException("Aula esgotada.Não há vagas disponiveis");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuarioLogado);
        reserva.setAula(aula);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void cancelarReserva(Long idReserva, Usuario usuarioLogado) {

        //busca a reserva
        Reserva reserva =reservaRepository.findById(idReserva).orElseThrow(
                () -> new EntityNotFoundException("Reserva com id #" +idReserva+"nao encotrada")
        );

        //verifica se o usuario é o dono da reserva
        if (!reserva.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Não é permitido de outro usuario");
        }
        reservaRepository.delete(reserva);
    }

    @Transactional(readOnly = true)
    public List<Reserva> buscarMinhasReservas(Usuario usuarioLogado) {
        return reservaRepository.findByUsuarioId(usuarioLogado.getId());
    }
}
