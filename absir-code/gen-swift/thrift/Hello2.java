package thrift;

import com.facebook.swift.codec.ThriftField.Requiredness;

import static com.google.common.base.MoreObjects.toStringHelper;

@ThriftStruct("Hello2")
public final class Hello2 {
    private final int id;
    private final long id2;

    @ThriftConstructor
    public Hello2(
            @ThriftField(value = 1, name = "id", requiredness = Requiredness.NONE) final int id,
            @ThriftField(value = 2, name = "id2", requiredness = Requiredness.NONE) final long id2
    ) {
        this.id = id;
        this.id2 = id2;
    }

    @ThriftField(value = 1, name = "id", requiredness = Requiredness.NONE)
    public int getId() {
        return id;
    }

    @ThriftField(value = 2, name = "id2", requiredness = Requiredness.NONE)
    public long getId2() {
        return id2;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("id2", id2)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Hello2 other = (Hello2) o;

        return
                Objects.equals(id, other.id) &&
                        Objects.equals(id2, other.id2);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{
                id,
                id2
        });
    }

    public static class Builder {
        private int id;
        private long id2;

        public Builder() {
        }

        public Builder(Hello2 other) {
            this.id = other.id;
            this.id2 = other.id2;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setId2(long id2) {
            this.id2 = id2;
            return this;
        }

        public Hello2 build() {
            return new Hello2(
                    this.id,
                    this.id2
            );
        }
    }
}
