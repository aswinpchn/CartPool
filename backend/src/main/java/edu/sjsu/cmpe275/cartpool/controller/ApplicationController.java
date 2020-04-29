package edu.sjsu.cmpe275.cartpool.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import edu.sjsu.cmpe275.cartpool.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import edu.sjsu.cmpe275.cartpool.Constants;
import edu.sjsu.cmpe275.cartpool.repository.StoreRepository;
import edu.sjsu.cmpe275.cartpool.repository.UserRepository;
import edu.sjsu.cmpe275.cartpool.service.ProductService;
import edu.sjsu.cmpe275.cartpool.service.StoreService;
import edu.sjsu.cmpe275.cartpool.service.UserService;
import edu.sjsu.cmpe275.cartpool.service.PoolService;

@Controller
@RequestMapping(path = "/cartpool")
@CrossOrigin(origins = Constants.BACKEND_URL)
public class ApplicationController {

	@Autowired
	private UserService userService;

	@Autowired
	private StoreService storeService;

	@Autowired
	private ProductService productService;

	@Autowired
	private PoolService poolService;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/store/all")
	@ResponseBody
	public List<Store> getAllStores() {
		return storeService.getAllStores();
	}

	@GetMapping("/product/{storeId}")
	@ResponseBody
	public List<Product> getProducts(@PathVariable("storeId") long storeId) {
		return productService.getProductsByStore(storeId);
	}

	@GetMapping("/user")
	@ResponseBody
	public User getUserByEmail(@RequestParam String email) {
		return userService.getUserByEmail(email);
	}

	@GetMapping("/search/{searchString}")
	@ResponseBody
	public Set<Product> search(@PathVariable("searchString") String searchString) {
		return productService.searchProducts(searchString);
	}

	@GetMapping("/user/verify")
	@ResponseBody
	public String verifyUser(@RequestParam String email, @RequestParam int code) {
		return userService.verifyUser(email, code);
	}

	@PostMapping("/user/updateProfile")
	@ResponseBody
	public User updateUserProfile(@Valid @RequestBody UpdateUserProfileRequestBodyModel request) {
		User user = new User();
		Address address = new Address();
		user.setEmail(request.getEmail());
		user.setScreenName(request.getScreenName());
		user.setNickName(request.getNickName());
		address.setStreet(request.getStreet());
		address.setCity(request.getCity());
		address.setState(request.getState());
		address.setZip(request.getZip());
		user.setAddress(address);
		return userService.updateUserProfile(user);
	}

	@PostMapping("/user/register")
	@ResponseBody
	public User createUser(@Valid @RequestBody CreateUserRequestBodyModel createUserRequestBody) {
		System.out.println("in api:    " + createUserRequestBody);
		User user = new User();
		if (createUserRequestBody.getPassword() != null) {
			user.setPassword(createUserRequestBody.getPassword());
		}
		user.setEmail(createUserRequestBody.getEmail());
		user.setUid(createUserRequestBody.getUid());

		return userService.createUser(user);
	}

	@PostMapping("/store/create")
	@ResponseBody
	public Store createStore(@Valid @RequestBody CreateStoreRequestBodyModel createStoreRequestBody) {
		System.out.println("in api:    " + createStoreRequestBody);
		Store store = new Store();
		Address address = new Address();
		store.setName(createStoreRequestBody.getName());
		address.setStreet(createStoreRequestBody.getStreet());
		address.setCity(createStoreRequestBody.getCity());
		address.setState(createStoreRequestBody.getState());
		address.setZip(createStoreRequestBody.getZip());
		store.setAddress(address);
		return storeService.createStore(store);
	}

	@PostMapping("/store/updateStore")
	@ResponseBody
	public Store updateStore(@Valid @RequestBody UpdateStoreRequestBodyModel request) {
		System.out.println("in api:    " + request);
		Store store = new Store();
		Address address = new Address();
		store.setName(request.getName());
		address.setStreet(request.getStreet());
		address.setCity(request.getCity());
		address.setState(request.getState());
		address.setZip(request.getZip());
		store.setAddress(address);
		return storeService.updateStore(store);
	}

	@DeleteMapping("/store/{id}")
	@ResponseBody
	public Store deleteStore(@PathVariable(required = true) long id) {
		System.out.println("in api deleteStore:" + id);
		return storeService.deleteStore(id);
	}

	@PostMapping("/product/add")
	@ResponseBody
	public Product addProduct(@Valid @ModelAttribute CreateProductRequestBodyModel createProductRequestBody) {
		System.out.println("in api:    " + createProductRequestBody);

		Optional<Store> storeObj = storeRepository.findStoreById(createProductRequestBody.getStoreId());

		if (storeObj.isPresent()) {
			Store store = storeObj.get();
			System.out.println("store => " + store);

			Product product = new Product();
			product.setStore(store);
			product.setSku(createProductRequestBody.getSku());
			product.setName(createProductRequestBody.getName());
			product.setDescription(createProductRequestBody.getDescription());
			product.setBrand(createProductRequestBody.getBrand());
			product.setPrice(createProductRequestBody.getPrice());
			product.setUnit(createProductRequestBody.getUnit());

			return productService.createProduct(product, createProductRequestBody.getImage());
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found");
	}

	@PostMapping("/product/edit/{productId}")
	@ResponseBody
	public Product editproduct(@PathVariable("productId") long productId,
			@Valid @RequestBody EditProductRequestBodyModel editProductRequestBodyModel) {
		Product product = new Product();
		product.setId(productId);
		product.setName(editProductRequestBodyModel.getName());
		product.setDescription(editProductRequestBodyModel.getDescription());
		product.setBrand(editProductRequestBodyModel.getBrand());
		product.setPrice(editProductRequestBodyModel.getPrice());
		product.setUnit(editProductRequestBodyModel.getUnit());
		product.setImageURL(editProductRequestBodyModel.getImageURL());
		return productService.editProduct(product);
	}

	@PostMapping("/pool/create")
	@ResponseBody
	public Pool createPool(@Valid @RequestBody CreatePoolRequestBodyModel createPoolRequestBodyModel) {
		System.out.println("createPoolRequestBodyModel:    " + createPoolRequestBodyModel);

		Optional<User> userObj = userRepository.findById(createPoolRequestBodyModel.getLeaderId());

		if (userObj.isPresent()) {
			User user = userObj.get();
			System.out.println("user => " + user);

			if(user.getPoolId() == null) {
				Pool pool = new Pool();
				pool.setPoolId(createPoolRequestBodyModel.getPoolId());
				pool.setName(createPoolRequestBodyModel.getName());
				pool.setNeighborhoodName(createPoolRequestBodyModel.getNeighborhoodName());
				pool.setDescription(createPoolRequestBodyModel.getDescription());
				pool.setZip(createPoolRequestBodyModel.getZip());
				pool.setLeaderId(createPoolRequestBodyModel.getLeaderId());

				return poolService.createPool(pool);
			} else {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already associated with an existing pool");
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User(Leader) not found");
	}

	@PostMapping("/pool/join")
	@ResponseBody
	public String joinPool(@Valid @RequestBody JoinPoolRequestBodyModel requestModel) {
		return poolService.joinPool(requestModel);
	}
	
	@GetMapping("/pool/joinrequest/{referrerScreenName}")
	@ResponseBody
	public List<PoolRequest> getPoolRequests(@PathVariable("referrerScreenName") String referrerScreenName) {
		return poolService.getPoolRequests(referrerScreenName);
	}
	
	@GetMapping("/pool/leader/joinrequest/{leaderScreenName}")
	@ResponseBody
	public List<PoolRequest> getPoolRequestsForLeader(@PathVariable("leaderScreenName") String leaderScreenName) {
		return poolService.getPoolRequestsForLeader(leaderScreenName);
	}
	
	@PostMapping("/pool/referral/approvejoinrequest/{requestId}")
	@ResponseBody
	public String approveReferralRequest(@PathVariable("requestId") long requestId) {
		return poolService.approveReferralRequest(requestId);
	}
	
	@PostMapping("/pool/leader/approvejoinrequest/{requestId}")
	@ResponseBody
	public String approveJoinRequestForLeader(@PathVariable("requestId") long requestId) {
		return poolService.approveJoinRequestForLeader(requestId);
	}
}