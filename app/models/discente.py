from __future__ import annotations
# O trecho `from __future__ import annotations` é uma diretiva do Python que ativa um comportamento futuro:
# permite que as anotações de tipos (type hints) sejam tratadas como strings,
# adiando sua avaliação.
# Isso é útil para evitar problemas de importação circular e
# para usar tipos que ainda não foram definidos no momento da declaração.
# É especialmente relevante em projetos que usam tipagem estática ou ferramentas como mypy.

from datetime import date, datetime
from typing import Optional

from sqlalchemy import (
    String,
    Integer,
    Boolean,
    Date,
    Text,
    ForeignKey,
    CheckConstraint,
    UniqueConstraint,
    func,
)
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base

class Discente(Base):
    """
    ORM para academic.discentes.
    Representa os discentes vinculados a um Programa de Pós-Graduação.
    Contém informações acadêmicas, status de matrícula e vínculo institucional.
    """

    __tablename__ = "discentes"
    # table_args define constraints and schema information for the table
    # __table_args__ = (


    # Chave Primária

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True,
    )

    # Relacionamentos
    usuario_id: Mapped[int] = mapped_column(ForeignKey("usuarios.usuario_id"), nullable=False)
    programa_id: Mapped[int] = mapped_column(ForeignKey("core.programas.id"), nullable=False)
    linha_pesquisa_id: Mapped[Optional[int]] = mapped_column(ForeignKey("core.linhas_pesquisa.id"), nullable=True)
    orientador_id: Mapped[Optional[int]] = mapped_column(ForeignKey("core.orientador.id"), nullable=True)
    coorientador_id: Mapped[Optional[int]] = mapped_column(ForeignKey("core.coorientador.id"), nullable=True)
    # O campo coorientador_id pode ser um inteiro (id do coorientador) ou None.
    # Isso significa que o discente pode ou não ter um coorientador vinculado.
    # O uso de Optional[int] permite essa flexibilidade, e nullable=True garante que o valor pode ser nulo no banco de dados.
    coorientador_id: Mapped[Optional[int]] = mapped_column(ForeignKey("core.coorientador.id"), nullable=True)

    # Dados de matrícula

    numero_matricula: Mapped[Optional[str]] = mapped_column(String(20), nullable=True)
    tipo_curso: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)  # Mestrado, Doutorado, etc.
    data_ingresso: Mapped[Optional[date]] = mapped_column(Date, nullable=True)
    data_primeira_matricula: Mapped[Optional[date]] = mapped_column(Date, nullable=True)

    # Dados do Projeto Inicial






