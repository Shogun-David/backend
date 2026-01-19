package com.indra.reservations_backend.commons.interfaces;

public interface ICrudCommonsDto<DTOReq, DTORes, ID> {
    public DTORes save(DTOReq request);
    public DTORes update(ID id, DTOReq request);
    public DTORes findById(ID id);
    public DTORes delete(ID id);   

}

