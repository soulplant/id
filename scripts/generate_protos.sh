mkdir -p gen
protoc protos/*.proto --java_out=gen
