import { combineReducers } from "redux";
import { authReducer } from "../components/_reducers/authReducer";
import { storeReducer } from "../components/_reducers/storeReducer";
import { errorReducer } from "../components/_reducers/errorReducer";
export const rootReducer = combineReducers({
  auth: authReducer,
  storeState: storeReducer,
  errorState: errorReducer,
});
