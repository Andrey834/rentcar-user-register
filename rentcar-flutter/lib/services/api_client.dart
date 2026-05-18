import 'package:dio/dio.dart';
import '../config/api_config.dart';

class ApiClient {
  static ApiClient? _instance;
  late final Dio _dio;

  ApiClient._() {
    _dio = Dio(
      BaseOptions(
        baseUrl: ApiConfig.baseUrl,
        connectTimeout: ApiConfig.connectTimeout,
        receiveTimeout: ApiConfig.receiveTimeout,
        headers: {'Content-Type': 'application/json'},
      ),
    );
    _dio.interceptors.add(
      LogInterceptor(requestBody: true, responseBody: true),
    );
  }

  factory ApiClient() => _instance ??= ApiClient._();

  Dio get dio => _dio;

  Future<Response> get(String path, {Map<String, dynamic>? params}) =>
      _dio.get(path, queryParameters: params);

  Future<Response> post(String path, {Object? data}) =>
      _dio.post(path, data: data);

  Future<Response> put(String path, {Object? data}) =>
      _dio.put(path, data: data);

  Future<Response> patch(String path, {Object? data, Map<String, dynamic>? params}) =>
      _dio.patch(path, data: data, queryParameters: params);

  Future<Response> delete(String path) => _dio.delete(path);
}
