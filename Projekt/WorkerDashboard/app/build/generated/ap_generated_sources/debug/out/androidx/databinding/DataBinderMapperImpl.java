package androidx.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new hu.pannon.workerdashboard.DataBinderMapperImpl());
  }
}
