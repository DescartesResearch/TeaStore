package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet implementation class Categorie
 */
@WebServlet("/category")
public class CategoryServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	private static final int INITIAL_PRODUCT_DISPLAY_COUNT = 20;
	private static final List<Integer> PRODUCT_DISPLAY_COUNT_OPTIONS = Arrays.asList(5, 10, 20, 30, 50);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CategoryServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		checkforCookie(request,response);
		long categoryID = Long.valueOf(request.getParameter("category"));

		Category category = LoadBalancedStoreOperations.getCategory(categoryID);

		

		int products = LoadBalancedStoreOperations.getNumberOfProducts(categoryID);

		int numberProducts = INITIAL_PRODUCT_DISPLAY_COUNT;
		if(request.getSession().getAttribute("numberProducts")!=null) {
			numberProducts = Integer.valueOf(request.getSession().getAttribute("numberProducts").toString());
		}
		
		int page = 1;
		if (request.getParameter("page") != null) {
			int pagenumber = Integer.valueOf(request.getParameter("page"));
			int maxpages = (int) Math.ceil(((double) products) / numberProducts);
			if(pagenumber <= maxpages) {
				page = pagenumber;
			}
		}
		
		ArrayList<String> navigation = createNavigation(products, page, numberProducts);

		List<Product> productlist = LoadBalancedStoreOperations.getProducts(categoryID, page, numberProducts);
		request.setAttribute("productImages", LoadBalancedImageOperations.getProductPreviewImages(productlist));
		request.setAttribute("storeIcon", LoadBalancedImageOperations.getWebImage("icon", ImageSize.ICON));
		request.setAttribute("CategoryList", LoadBalancedStoreOperations.getCategories());
		request.setAttribute("title", "Pet Supply Store Categorie " + category.getName());

		request.setAttribute("Productslist", productlist);

		request.setAttribute("category", category.getName());
		request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));
		request.setAttribute("categoryID", categoryID);
		request.setAttribute("currentnumber", numberProducts);
		request.setAttribute("pagination", navigation);
		request.setAttribute("pagenumber", page);
		request.setAttribute("productdisplaycountoptions", PRODUCT_DISPLAY_COUNT_OPTIONS);
		request.getRequestDispatcher("WEB-INF/pages/category.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameter("number") != null && request.getParameter("page") != null
				&& request.getParameter("category") != null) {
			redirect("/category?category=" + request.getParameter("category") + "&page=" + request.getParameter("page"),
					response, PRODUCTCOOKIE, request.getParameter("number"));
		} else {
			doGet(request, response);
		}
	}

	private ArrayList<String> createNavigation(int products, int page, int numberProducts) {

		ArrayList<String> navigation = new ArrayList<String>();

		int numberpagination = 5;

		int maxpages = (int) Math.ceil(((double) products) / numberProducts);

		if (maxpages < page) {
			return navigation;
		}

		if (page == 1) {
			if (maxpages == 1) {
				navigation.add("1");
				return navigation;
			}
			int min = Math.min(maxpages, numberpagination + 1);
			for (int i = 1; i <= min; i++) {
				navigation.add(String.valueOf(i));
			}

		} else {
			navigation.add("previous");
			if (page == maxpages) {
				int max = Math.max(maxpages - numberpagination, 1);
				for (int i = max; i <= maxpages; i++) {
					navigation.add(String.valueOf(i));
				}
				return navigation;

			} else {
				int lowerbound = (int) Math.ceil((numberpagination-1)/2);
				int upperbound = (int) Math.floor((numberpagination-1)/2);
				int up = Math.min(page+upperbound, maxpages);
				int down = Math.max(page-lowerbound, 1); 
				for (int i = down; i <= up; i++) {
					navigation.add(String.valueOf(i));
				}
			}
		}
		navigation.add("next");

		return navigation;
	}

}
