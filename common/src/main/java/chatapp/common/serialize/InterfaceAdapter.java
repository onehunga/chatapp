package chatapp.common.serialize;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class InterfaceAdapter<T> extends TypeAdapter<T> {
	@Override
	public void write(JsonWriter out, T value) throws IOException {
		if(value == null) {
			out.nullValue();
			return;
		}

		out.value(value.getClass().getName());
		Gson gson = new Gson();
		gson.toJson(value, value.getClass(), out);
	}

	@Override
	public T read(JsonReader in) throws IOException {
		if(in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}

		Gson gson = new Gson();
		String className = in.nextString();
		try {
			Class<? extends T> implClass = (Class<? extends T>) Class.forName(className);
			T instance = gson.fromJson(in, implClass);
			return instance;
		} catch(ClassNotFoundException e) {
			throw new IOException("failed to ", e);
		}
	}
}
