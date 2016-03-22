package com.absir.server.converter;

import com.absir.aserv.system.server.ServerResolverBody;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.context.core.ContextUtils;
import com.absir.data.value.IProto;
import com.absir.server.on.OnPut;
import com.absir.server.route.body.IBodyConverter;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by absir on 16/3/22.
 */
@Base
@Bean
public class ProtoBodyConverter implements IBodyConverter {

    @Override
    public String[] getContentTypes() {
        return null;
    }

    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, String input, Class<?> parameterType) throws Exception {
        if (IProto.class.isAssignableFrom(parameterType)) {
            Codec<?> codec = ProtobufProxy.create(parameterType);
            return codec.decode(input.getBytes(ContextUtils.getCharset()));
        }

        return ServerResolverBody.ME.readBodyParameterValue(onPut, group, input, parameterType);
    }

    @Override
    public Object readBodyParameterValue(OnPut onPut, int group, InputStream inputStream, Class<?> parameterType) throws Exception {
        if (IProto.class.isAssignableFrom(parameterType)) {
            Codec<?> codec = ProtobufProxy.create(parameterType);
            return codec.readFrom(CodedInputStream.newInstance(inputStream));
        }

        return ServerResolverBody.ME.readBodyParameterValue(onPut, group, inputStream, parameterType);
    }

    @Override
    public byte[] writeAsBytes(OnPut onPut, Object returnValue) throws Exception {
        if (returnValue instanceof IProto) {
            Codec<Object> codec = (Codec<Object>) ProtobufProxy.create(returnValue.getClass());
            return codec.encode(returnValue);
        }

        return ServerResolverBody.ME.writeAsBytes(onPut, returnValue);
    }

    @Override
    public void writeValue(OnPut onPut, Object returnValue, OutputStream outputStream) throws Exception {
        if (returnValue instanceof IProto) {
            Codec<Object> codec = (Codec<Object>) ProtobufProxy.create(returnValue.getClass());
            codec.writeTo(returnValue, CodedOutputStream.newInstance(outputStream));
            return;
        }

        ServerResolverBody.ME.writeValue(onPut, returnValue, outputStream);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
