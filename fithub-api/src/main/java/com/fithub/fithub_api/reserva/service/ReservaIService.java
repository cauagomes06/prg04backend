package com.fithub.fithub_api.reserva.service;

import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.usuario.entity.Usuario;

import java.util.List;

public interface ReservaIService {

    public void realizarCheckIn(Long idReserva, Usuario usuarioLogado);

    public void cancelarReserva(Long idReserva, Usuario usuarioLogado);

    public Reserva criarReserva(Long idAula, Usuario usuarioLogado);

    public List<Reserva> buscarMinhasReservas(Usuario usuarioLogado);
}
