package com.absir.aserv.configure.xls.value;

import java.util.List;

public interface IExportList<T> {

    List<Object> exportDts(List<T> entities, int type);

}
