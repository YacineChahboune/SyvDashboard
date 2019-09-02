package dz.saticom.syvcom.dashboard.repositories;

import java.util.List;

public interface CustomNativeRepository {

	List<Object> runNativeQueryList(String query);
	Object runNativeQueryOne(String query);
	Object runNativeQueryFirst(String query);
}
